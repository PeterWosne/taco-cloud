package ru.ermakow.tacocloud.repositories;

import ru.ermakow.tacocloud.TacoOrder;

public interface OrderRepository {
    TacoOrder save(TacoOrder order);
}
