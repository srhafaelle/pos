package pos.pos.service;

import pos.pos.entities.StockMovement;

import java.util.List;

public interface StockMovementService {
  void createMovement(StockMovement stockMovement);
  List<StockMovement> findByProductId(String productId);
  List<StockMovement> findAllMovements();
}
