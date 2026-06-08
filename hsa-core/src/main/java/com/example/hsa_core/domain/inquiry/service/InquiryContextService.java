package com.example.hsa_core.domain.inquiry.service;

import com.example.hsa_core.domain.customer.Customer;
import com.example.hsa_core.domain.customer.CustomerRepository;
import com.example.hsa_core.domain.inquiry.dto.InquiryContextResponse;
import com.example.hsa_core.domain.mock.MockDelivery;
import com.example.hsa_core.domain.mock.MockOrder;
import com.example.hsa_core.domain.mock.repository.MockDeliberyRepository;
import com.example.hsa_core.domain.mock.repository.MockOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryContextService {

    private final CustomerRepository customerRepository;
    private final MockOrderRepository mockOrderRepository;
    private final MockDeliberyRepository mockDeliberyRepository;


    public InquiryContextResponse buildContext(Long customerId){

        Customer customer = customerRepository.findById(customerId)
                .orElse(null);

        if (customer == null){
            return InquiryContextResponse.empty();
        }

        List<MockOrder> orders = mockOrderRepository.findByCustomerOrderByOrderedAtDesc(customer);
        if(orders.isEmpty()){
            return InquiryContextResponse.empty();
        }
        MockOrder latestOrder = orders.get(0);

        MockDelivery delivery = mockDeliberyRepository.findByCustomer(customer).orElse(null);

        return new InquiryContextResponse(
                latestOrder.getOrderStatus() != null ? latestOrder.getOrderStatus().name() : "UNKNOWN",
                delivery != null ? delivery.getTrackingNumber() : "미발급",
                delivery != null ? delivery.getCarrier() : "정보없음",
                latestOrder.getProductName() != null ? latestOrder.getProductName() : "상품정보없음",
                delivery != null && delivery.getDeliveryStatus() != null ? delivery.getDeliveryStatus().name() : "NONE",
                delivery != null ? delivery.getCurrentLocation() : "정보없음"
        );
    }
}
