package com.faustas.dbms;

import com.faustas.dbms.framework.ApplicationContext;
import com.faustas.dbms.models.Ingredient;
import com.faustas.dbms.repositories.IngredientsRepository;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext(Main.class, "resources/application.properties");
        IngredientsRepository ingredientsRepository = context.getBean(IngredientsRepository.class);

        for (Ingredient ingredient : ingredientsRepository.findAll()) {
            System.out.println(ingredient);
        }
    }
}
