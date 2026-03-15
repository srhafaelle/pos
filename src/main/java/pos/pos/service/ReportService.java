package pos.pos.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.pos.dto.DailySalesDTO;
import pos.pos.dto.TopProductDTO;
import pos.pos.entities.Sale;
import pos.pos.repository.SaleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private SaleRepository saleRepo;

    public List<TopProductDTO> getTopSellingProducts() {
        // 1. Obtener todas las ventas (Idealmente filtrar por fecha, ej: último mes)
        List<Sale> allSales = saleRepo.findAll();

        // Mapa para acumular: NombreProducto -> DTO
        Map<String, TopProductDTO> productMap = new HashMap<>();

        for (Sale sale : allSales) {
            for (Sale.SaleItem item : sale.getItems()) {
                String name = item.getProductName();

                // Si ya existe en el mapa, lo actualizamos
                if (productMap.containsKey(name)) {
                    TopProductDTO existing = productMap.get(name);
                    existing.setQuantitySold(existing.getQuantitySold() + item.getQuantity());
                    existing.setTotalRevenueUsd(existing.getTotalRevenueUsd() + item.getSubTotal());
                } else {
                    // Si no existe, lo creamos
                    productMap.put(name, new TopProductDTO(name, item.getQuantity(), item.getSubTotal()));
                }
            }
        }

        // 2. Convertir a lista y ORDENAR de Mayor a Menor cantidad
        return productMap.values().stream()
                .sorted((p1, p2) -> p2.getQuantitySold().compareTo(p1.getQuantitySold())) // Descendente
                .limit(10) // Solo el Top 10
                .collect(Collectors.toList());
    }

    // ... imports (java.time.*)

    public List<DailySalesDTO> getLast7DaysSales() {
        List<DailySalesDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Iteramos desde hace 6 días hasta hoy (7 días total)
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            // Definir inicio y fin del día para la búsqueda
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            // Buscar ventas en ese rango
            List<Sale> sales = saleRepo.findByTimestampBetween(startOfDay, endOfDay);

            // Sumar total USD
            double dailyTotal = sales.stream().mapToDouble(Sale::getTotalUsd).sum();

            // Obtener nombre del día (Ej: "MON", "TUE")
            String dayName = date.getDayOfWeek().toString().substring(0, 3);

            result.add(new DailySalesDTO(dayName, dailyTotal));
        }
        return result;
    }
}