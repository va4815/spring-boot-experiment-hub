BEGIN;

WITH new_restaurant AS (
INSERT INTO t_restaurant (
    name,
    restaurant_type,
    location
)
VALUES (
    'Seventh Heaven',
    'Bar',
    'Sector 7 slums of Midgar'
    )
    RETURNING id
    ),
    new_menu AS (
INSERT INTO t_menu (
    restaurant_id,
    is_active
)
SELECT id, TRUE
FROM new_restaurant
    RETURNING id
    )
INSERT INTO t_menu_item (
    menu_id,
    name,
    description,
    item_type,
    category,
    price
)
SELECT
    new_menu.id,
    item.name,
    item.description,
    item.item_type,
    item.category,
    item.price
FROM new_menu
         CROSS JOIN (
    VALUES
        ('Eggs',         'Steam egg',            'Snacks',      'Food',  1.99),
        ('Chips',        'Good with Beers',       'Snacks',      'Food',  7.99),
        ('Pizza',        '14 inch pizza',         'Main Course', 'Food', 15.99),
        ('Orange Juice', 'Fresh orange juice',    'Drink',       'Drink', 3.99),
        ('Beers',        'Draft Beers',           'Drink',       'Drink', 6.99),
        ('Whiskey',      'Tifa special drink',    'Drink',       'Drink', 9.99)
) AS item(name, description, item_type, category, price);

COMMIT;