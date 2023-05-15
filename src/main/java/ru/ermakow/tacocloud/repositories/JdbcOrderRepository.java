package ru.ermakow.tacocloud.repositories;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ermakow.tacocloud.Ingredient;
import ru.ermakow.tacocloud.Taco;
import ru.ermakow.tacocloud.TacoOrder;

import java.sql.Types;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcOrderRepository implements OrderRepository{

    JdbcOperations jdbcOperations;

    @Autowired
    public JdbcOrderRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    @Transactional
    public TacoOrder save(TacoOrder order) {
        // подготовим sql запрос типа insert в таблицу Taco_Order
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "insert into Taco_Order" +
                        "(d_name,d_street,d_city,d_state,d_zip,cc_number,cc_expiration,cc_cvv,placed_at) " +
                        "values(?,?,?,?,?,?,?,?,?);",
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP
        );
        pscf.setReturnGeneratedKeys(true); // поставим в тру возвращение сгенерированного ключа

        // укажем время создания заказа
        order.setPlacedAt(new Date());

        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(List.of(
                order.getDeliveryName(),
                order.getDeliveryStreet(),
                order.getDeliveryCity(),
                order.getDeliveryState(),
                order.getDeliveryZip(),
                order.getCcNumber(),
                order.getCcExpiration(),
                order.getCcCVV(),
                order.getPlacedAt()
        ));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        // делаем update(сохраняем заказ в БД)
        jdbcOperations.update(psc, keyHolder);

        // получаем id заказа(так как в БД он имеет тип identity и база назначает его автоматически, нам надо было его получить)
        long orderId = keyHolder.getKey().longValue();
        order.setId(orderId);

        List<Taco> tacos = order.getTacos();
        for (Taco taco : tacos) {
            // сохраним все имеющиеся в заказе тако в таблицу Taco -> saveTaco(taco, id)
            saveTaco(taco, order.getId());
        }
        return order;
    }

    private long saveTaco(Taco taco, long orderId) {
        Date date = new Date();

        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
                "insert into Taco(name, taco_order, created_at) values(?,?,?)",
                Types.VARCHAR, Type.LONG, Types.TIMESTAMP
        );
        pscf.setReturnGeneratedKeys(true);

        PreparedStatementCreator psc = pscf.newPreparedStatementCreator(List.of(
                taco.getName(),
                orderId,
                date
        ));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcOperations.update(psc, keyHolder);

        long tacoId = keyHolder.getKey().longValue();
        taco.setId(tacoId);

        List<Ingredient> ingredients = taco.getIngredients();
        for (Ingredient ingredient : ingredients) {
            saveIngredient(ingredient, taco.getId());
        }
        return tacoId;
    }

    private void saveIngredient(Ingredient ingredient, long id) {
        jdbcOperations.update("insert into Ingredient_Ref(ingredient, taco) values(?,?)", ingredient.getId(), id);
    }
}
