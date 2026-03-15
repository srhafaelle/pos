package pos.pos.service;


import pos.pos.dto.SaleRequest;
import pos.pos.entities.Sale;

public interface SaleService {
    Sale processSale(SaleRequest request);
}
