package pos.pos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pos.pos.entities.StockMovement;
import pos.pos.repository.StockMovementRepository;
import pos.pos.service.StockMovementService;


import java.util.ArrayList;
import java.util.List;
@Service
public class StockMovementServiceImpl implements StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Override
    public void createMovement(StockMovement stockMovement) {
        stockMovementRepository.save(stockMovement);
    }

    @Override
    public List<StockMovement> findByProductId(String productId) {
        return stockMovementRepository.findByProductIdOrderByTimestampDesc(productId);
    }

    @Override
    public List<StockMovement> findAllMovements() {
        return stockMovementRepository.findAll();
    }
}