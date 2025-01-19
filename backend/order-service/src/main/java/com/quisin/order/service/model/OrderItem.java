package com.quisin.order.service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "order")
@ToString(exclude = "order")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "menu_item_id", nullable = false)
    private Long menuItemId;

    @Column(name = "menu_item_name", nullable = false)
    private String menuItemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "special_instructions")
    private String specialInstructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderItemStatus status;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = OrderItemStatus.PENDING;
        }
        calculateSubtotal();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateSubtotal();
    }

    public void calculateSubtotal() {
        if (quantity != null && unitPrice != null) {
            subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
} 