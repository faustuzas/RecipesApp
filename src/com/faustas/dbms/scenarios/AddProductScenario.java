package com.faustas.dbms.scenarios;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.models.Product;
import com.faustas.dbms.services.ConsoleInteractor;
import com.faustas.dbms.services.ProductService;
import com.faustas.dbms.utils.NumberReader;

@Service
public class AddProductScenario extends ConsoleScenario {

    private ProductService productService;

    private NumberReader numberReader;

    public AddProductScenario(ConsoleInteractor interactor, ProductService productService,
                              NumberReader numberReader) {
        super(interactor);
        this.productService = productService;
        this.numberReader = numberReader;
    }

    @Override
    public boolean action() {
        interactor.printHeader("Add new product");

            Product product = new Product();
            product.setName(interactor.ask("Product name:"));

            interactor.print("Carbohydrates per 100g:");
            product.setCarbohydrates(numberReader.readDouble("Enter number from 0 to 100"));

            interactor.print("Proteins per 100g:");
            product.setProteins(numberReader.readDouble("Enter number from 0 to 100"));

            interactor.print("Fats per 100g:");
            product.setFats(numberReader.readDouble("Enter number from 0 to 100"));

        try {
            productService.insert(product);
            interactor.printSuccess(String.format("Product \"%s\" added successfully", product.getName()));
        } catch (Exception e) {
            interactor.printError(e.getMessage());
        }

        return true;
    }
}
