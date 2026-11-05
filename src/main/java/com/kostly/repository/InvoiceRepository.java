package com.kostly.repository;

import com.kostly.entity.Invoice;
import com.kostly.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findAllByOrderByIdDesc();
    long countByStatus(InvoiceStatus status);
}
