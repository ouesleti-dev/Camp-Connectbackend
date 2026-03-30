package org.example.campconnect.Service;

import org.example.campconnect.Entity.*;
import org.example.campconnect.Repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.campconnect.dto.OrderLineRequest;
import org.example.campconnect.dto.OrderRequest;
import org.example.campconnect.dto.OrderResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    @Override
    @Transactional
    public Order createOrder(OrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Save order first (without lines)
        Order order = Order.builder()
                .orderDate(new Date())
                .deliveryAddress(request.getDeliveryAddress())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(0.0)
                .user(user)
                .orderLines(new ArrayList<>())
                .build();

        Order savedOrder = orderRepository.save(order);

        // 2. Build order lines
        List<OrderLine> lines = new ArrayList<>();
        double total = 0.0;

        for (OrderLineRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            if (product.getQuantityProduct() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getNameProduct());
            }

            // Decrease stock
            product.setQuantityProduct(product.getQuantityProduct() - item.getQuantity());
            productRepository.save(product);

            double unitPrice = product.getPriceProduct();
            double lineTotal = unitPrice * item.getQuantity();
            total += lineTotal;

            OrderLine line = OrderLine.builder()
                    .product(product)
                    .requestedQuantity(item.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(lineTotal)
                    .build();

            lines.add(orderLineRepository.save(line));
        }

        // 3. Update order with lines and total
        savedOrder.setOrderLines(lines);
        savedOrder.setTotalAmount(total);
        return orderRepository.save(savedOrder);
    }
    @Override
    @Transactional (readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdUser(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toDTO(order);
    }
    public OrderResponseDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus(status);
        return toDTO(orderRepository.save(order));
    }
    @Override
    @Transactional
    public OrderResponseDTO approveOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("APPROVED");
        Order saved = orderRepository.save(order);
        String sellerAddress = "Unknown";
        if (saved.getOrderLines() != null && !saved.getOrderLines().isEmpty()) {
            Product firstProduct = saved.getOrderLines().get(0).getProduct();
            if (firstProduct != null && firstProduct.getLocationProduct() != null) {
                sellerAddress = firstProduct.getLocationProduct();
            }
        }


        Delivery delivery = Delivery.builder()
                .departureAddress(sellerAddress)
                .arrivalAddress(order.getDeliveryAddress())
                .deliveryFee(7.0)
                .deliverystate(DeliveryState.PENDING)
                .order(saved)
                .build();
        deliveryRepository.save(delivery);

        return toDTO(saved);
    }
    @Override
    public OrderResponseDTO rejectOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setOrderStatus("REJECTED");
        return toDTO(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private OrderResponseDTO toDTO(Order order) {
        List<OrderResponseDTO.OrderLineDTO> lineDTOs = order.getOrderLines().stream()
                .map(line -> OrderResponseDTO.OrderLineDTO.builder()
                        .idOrderLine(line.getIdOrderLine())
                        .requestedQuantity(line.getRequestedQuantity())
                        .unitPrice(line.getUnitPrice())
                        .totalPrice(line.getTotalPrice())
                        .productName(line.getProduct() != null ? line.getProduct().getNameProduct() : null)
                        .productPrice(line.getProduct() != null ? line.getProduct().getPriceProduct() : null)
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .idOrder(order.getIdOrder())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .paymentMethod(order.getPaymentMethod())
                .orderStatus(order.getOrderStatus())
                .userFirstName(order.getUser() != null ? order.getUser().getFirstName() : null)
                .userLastName(order.getUser() != null ? order.getUser().getLastName() : null)
                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .customerPhone(order.getUser() != null ? order.getUser().getPhone() : null)
                .orderLines(lineDTOs)
                .build();
    }

}
