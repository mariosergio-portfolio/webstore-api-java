package com.mycompany.webstore.application.service;

import com.mycompany.webstore.application.port.in.ReportPort;
import com.mycompany.webstore.application.port.out.ReportQueryRepository;
import com.mycompany.webstore.domain.model.Category;
import com.mycompany.webstore.domain.model.ProductWithCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportPortImpl implements ReportPort {

    private final ReportQueryRepository reportQueryRepository;

    public ReportPortImpl(ReportQueryRepository reportQueryRepository) {
        this.reportQueryRepository = reportQueryRepository;
    }

    @Override
    public List<Category> listCategories() {
        return reportQueryRepository.findAllCategories();
    }

    @Override
    public List<ProductWithCategory> listProductsWithCategory() {
        return reportQueryRepository.findAllProductsWithCategory();
    }
}
