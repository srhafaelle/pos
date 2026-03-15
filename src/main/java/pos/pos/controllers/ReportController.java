package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pos.pos.dto.DailySalesDTO;
import pos.pos.dto.TopProductDTO;
import pos.pos.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/top-products")
    public List<TopProductDTO> getTopProducts() {

        return reportService.getTopSellingProducts();
    }

    @GetMapping("/weekly-sales")
    public List<DailySalesDTO> getWeeklySales() {
        return reportService.getLast7DaysSales();
    }
}