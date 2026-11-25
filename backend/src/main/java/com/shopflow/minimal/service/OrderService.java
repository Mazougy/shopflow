package com.shopflow.minimal.service;

import com.shopflow.minimal.dto.order.OrderResponse;
import com.shopflow.minimal.entity.Cart;
import com.shopflow.minimal.entity.CartItem;
import com.shopflow.minimal.entity.Order;
import com.shopflow.minimal.entity.OrderItem;
import com.shopflow.minimal.entity.OrderStatus;
import com.shopflow.minimal.entity.Product;
import com.shopflow.minimal.entity.Role;
import com.shopflow.minimal.entity.User;
import com.shopflow.minimal.exception.BadRequestException;
import com.shopflow.minimal.exception.ResourceNotFoundException;
import com.shopflow.minimal.mapper.OrderMapper;
import com.shopflow.minimal.mapper.ProductMapper;
import com.shopflow.minimal.repository.OrderRepository;
import com.shopflow.minimal.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final UserService userService;

    @Transactional
    public OrderResponse checkout(String email) {
        User user = userService.getByEmail(email);
        Cart cart = cartService.getOrCreateCart(email);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new BadRequestException("Cart contains inactive products");
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            BigDecimal unitPrice = ProductMapper.resolveCurrentPrice(product);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subTotal = subTotal.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .build();
            orderItems.add(orderItem);
        }

        BigDecimal shippingFee = cartService.resolveShippingFee(cart);
        BigDecimal total = subTotal.add(shippingFee);

        Order order = Order.builder()
                .user(user)
                .orderNumber("TEMP-" + UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .subTotal(subTotal)
                .shippingFee(shippingFee)
                .totalTTC(total)
                .build();
        order = orderRepository.save(order);

        order.setOrderNumber("ORD-" + Year.now().getValue() + "-" + String.format("%05d", order.getId()));
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            order.getItems().add(orderItem);
        }

        order = orderRepository.save(order);
        cartService.clearCart(cart);

        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User user = userService.getByEmail(email);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String email, Long orderId) {
        return OrderMapper.toResponse(getOrderEntity(email, orderId));
    }

    @Transactional
    public OrderResponse cancelOrder(String email, Long orderId) {
        Order order = getOrderEntity(email, orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    private Order getOrderEntity(String email, Long orderId) {
        User user = userService.getByEmail(email);
        if (user.getRole() == Role.ADMIN) {
            return orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        }
        return orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
