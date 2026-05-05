package org.example.campconnect.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.campconnect.Entity.DeliveryState;
import org.example.campconnect.Entity.Order;
import org.example.campconnect.Repository.DeliveryRepository;
import org.example.campconnect.Repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processDailyMarketplaceUpdates() {
        Date now = new Date();
        cancelOverdueDeliveries(now);
        autoRejectStalePendingOrders(now);
    }


    private void cancelOverdueDeliveries(Date now) {
        deliveryRepository.findByDeliverystate(DeliveryState.ON_THE_WAY)
                .stream()
                .filter(d -> d.getEstimatedDeliveryDate() != null
                        && d.getEstimatedDeliveryDate().before(now))
                .forEach(d -> {
                    d.setDeliverystate(DeliveryState.CANCELLED);
                    deliveryRepository.save(d);
                    log.info("[SCHEDULER] Delivery #{} cancelled — overdue since {}",
                            d.getIdDelivery(), d.getEstimatedDeliveryDate());
                });
    }


    private void autoRejectStalePendingOrders(Date now) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date sevenDaysAgo = cal.getTime();

        List<Order> pendingOrders = orderRepository.findByOrderStatus("PENDING");
        pendingOrders.stream()
                .filter(o -> o.getOrderDate() != null
                        && o.getOrderDate().before(sevenDaysAgo))
                .forEach(o -> {
                    o.setOrderStatus("REJECTED");
                    orderRepository.save(o);
                    log.info("[SCHEDULER] Order #{} auto-rejected — pending since {}",
                            o.getIdOrder(), o.getOrderDate());
                });
    }
}