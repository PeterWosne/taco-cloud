package ru.ermakow.tacocloud.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.ermakow.tacocloud.Ingredient;
import ru.ermakow.tacocloud.Taco;
import ru.ermakow.tacocloud.TacoOrder;
import ru.ermakow.tacocloud.repositories.IngredientRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder") // объект tacoOrder должен поддерживаться на уровне сессии
public class DesignTacoController {

    private IngredientRepository ingredientRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        List<Ingredient> ingredients = ingredientRepo.findAll();

        Ingredient.Type[] types = Ingredient.Type.values();
        for (Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
    }

    //параллельно с тем что покажем вьюшку design создадим два объекта -> taco и tacoOrder
    @ModelAttribute(name = "tacoOrder")
    public TacoOrder order() {
        return new TacoOrder();
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    @GetMapping
    public String showDesignTaco() {
        return "design";
    }

    @PostMapping
    public String processTaco(@Valid Taco taco, Errors errors, @ModelAttribute TacoOrder tacoOrder) {
        if(errors.hasErrors()) {
            return "/design";
        }

        tacoOrder.addTaco(taco);
        log.info("Taco added: {}", taco);
        return "redirect:/orders/current";
    }

    //TODO создать метод processTaco

    private List<Ingredient> filterByType(List<Ingredient> list, Ingredient.Type type) {
        return list.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
