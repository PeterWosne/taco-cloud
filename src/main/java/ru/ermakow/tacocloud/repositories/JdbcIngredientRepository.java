package ru.ermakow.tacocloud.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ermakow.tacocloud.Ingredient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcIngredientRepository implements IngredientRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    // начиная с Spring 4.0 @Autowired можно не ставить, если конструктор единственный в классе

    @Override
    public List<Ingredient> findAll() {
        return jdbcTemplate.query(
                "select id,name,type from Ingredient",
                this::mapRowToIngredient
        );
    }

    @Override
    public Optional<Ingredient> findById(String id) {
        List<Ingredient> list = jdbcTemplate.query(
                "select id,name,type from Ingredient where id=?",
                this::mapRowToIngredient,
                id
        );
        return list.size() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        jdbcTemplate.update(
          "insert into Ingredient(id, name, type) values(?,?,?)",
          ingredient.getId(),
          ingredient.getName(),
          ingredient.getType()
        );
        return ingredient;
    }

    private Ingredient mapRowToIngredient(ResultSet row, int rowNum) throws SQLException {
        return new Ingredient(
                row.getString("id"),
                row.getString("name"),
                Ingredient.Type.valueOf(row.getString("type")));
    }
    // jdbcTemplate.query принимает sql-запрос, реализацию RowMapper которая маппит запись из набора результатов в объект,опционально параметры
    // можно передать не лямбду, а чистый RowMapper, тогда использовать -> queryForObject()

    // TODO  !!!  т.к работаем с JdbcTemplate -> определим схему самостоятельно(SB найдет файл schema.sql в src/main/resources и выполнит при загрузке приложения)
    // тоже самое для data.sql(в него положим данные)
}
