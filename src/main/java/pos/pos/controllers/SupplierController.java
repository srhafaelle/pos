package pos.pos.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pos.pos.entities.Supplier;
import pos.pos.repository.SupplierRepository;
import pos.pos.service.SupplierService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SupplierRepository supplierRepo;

    // Listar todos
    @GetMapping
    public List<Supplier> listAll() {
        return supplierService.getAllSuppliers();
    }

    // Crear proveedor
    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierRepo.save(supplier);
    }

    // Registrar vacíos (botellas)
    @PostMapping("/{id}/empties")
    public void addEmpties(@PathVariable String id, @RequestParam Integer qty) {
        supplierService.registerEmptyBottles(id, qty);
    }
}