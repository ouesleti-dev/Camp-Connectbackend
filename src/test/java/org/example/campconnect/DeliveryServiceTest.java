package org.example.campconnect;
import org.example.campconnect.Entity.*;
import org.example.campconnect.Entity.Order;
import org.example.campconnect.Repository.*;
import org.example.campconnect.Service.DeliveryService;
import org.example.campconnect.dto.DeliveryResponseDTO;
import org.example.campconnect.dto.TakeDeliveryRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock private DeliveryRepository deliveryRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private DeliveryService deliveryService;

    private User mockUser;
    private Delivery mockDelivery;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .idUser(1L)
                .firstName("Ali")
                .lastName("Ben")
                .phone("12345678")
                .build();

        mockOrder = Order.builder()
                .idOrder(1L)
                .deliveryAddress("Sousse")
                .totalAmount(200.0)
                .orderDate(new Date())
                .user(mockUser)
                .build();

        mockDelivery = Delivery.builder()
                .idDelivery(1L)
                .departureAddress("Tunis")
                .arrivalAddress("Sousse")
                .deliveryFee(5.0)
                .deliverystate(DeliveryState.PENDING)
                .order(mockOrder)
                .build();
    }

    @Test
    @DisplayName("Should return pending deliveries")
    void testGetPendingDeliveries() {
        when(deliveryRepository.findByDeliverystate(DeliveryState.PENDING))
                .thenReturn(List.of(mockDelivery));

        List<DeliveryResponseDTO> result = deliveryService.getPendingDeliveries();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getDeliveryState());
    }

    @Test
    @DisplayName("Should take delivery successfully")
    void testTakeDelivery_Success() {
        TakeDeliveryRequest request = new TakeDeliveryRequest(1L, new Date());

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(mockDelivery));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(mockDelivery);

        DeliveryResponseDTO result = deliveryService.takeDelivery(1L, request);

        assertEquals("ON_THE_WAY", mockDelivery.getDeliverystate().name());
        assertEquals(mockUser, mockDelivery.getDeliveryPerson());
        verify(deliveryRepository).save(mockDelivery);
    }

    @Test
    @DisplayName("Should throw exception when delivery not PENDING")
    void testTakeDelivery_NotAvailable() {
        mockDelivery.setDeliverystate(DeliveryState.ON_THE_WAY);
        TakeDeliveryRequest request = new TakeDeliveryRequest(1L, new Date());

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(mockDelivery));

        assertThrows(RuntimeException.class,
                () -> deliveryService.takeDelivery(1L, request));
    }

    @Test
    @DisplayName("Should mark delivery as delivered")
    void testMarkDelivered_Success() {
        mockDelivery.setDeliveryPerson(mockUser);
        mockDelivery.setDeliverystate(DeliveryState.ON_THE_WAY);

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(mockDelivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(mockDelivery);

        DeliveryResponseDTO result = deliveryService.markDelivered(1L, 1L);

        assertEquals("DELIVERED", mockDelivery.getDeliverystate().name());
    }

    @Test
    @DisplayName("Should throw when wrong delivery person tries to mark delivered")
    void testMarkDelivered_WrongPerson() {
        User otherUser = User.builder().idUser(99L).build();
        mockDelivery.setDeliveryPerson(otherUser);

        when(deliveryRepository.findById(1L)).thenReturn(Optional.of(mockDelivery));

        assertThrows(RuntimeException.class,
                () -> deliveryService.markDelivered(1L, 1L));
    }

    @Test
    @DisplayName("Should return my deliveries")
    void testGetMyDeliveries() {
        when(deliveryRepository.findByDeliveryPersonIdUser(1L))
                .thenReturn(List.of(mockDelivery));

        List<DeliveryResponseDTO> result = deliveryService.getMyDeliveries(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}