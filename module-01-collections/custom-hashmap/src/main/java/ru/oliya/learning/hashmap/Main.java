package ru.oliya.learning.hashmap;

public class Main {
    public static void main(String[] args) {
        CustomHashMap<String, Integer> map =
                new CustomHashMap<>(2, 0.75f);
        System.out.println("Начальная ёмкость: " + map.capacity());
        map.put("Aa", 100);
        map.put("BB", 200);
        System.out.println("\nПосле добавления элементов:");
        System.out.println("Значение по ключу Aa: " + map.get("Aa"));
        System.out.println("Значение по ключу BB: " + map.get("BB"));
        System.out.println("Ёмкость после расширения: " + map.capacity());

        map.put("Ключ", 300);
        Integer oldValue = map.put("Aa", 150);

        System.out.println("\nПосле обновления значения:");
        System.out.println("Предыдущее значение Aa: " + oldValue);
        System.out.println("Новое значение Aa: " + map.get("Aa"));
        System.out.println("\nПроверка наличия ключа Java: "
                + map.containsKey("Java"));

        Integer removedValue = map.remove("BB");

        System.out.println("\nУдаление элемента:");
        System.out.println("Удалённое значение BB: " + removedValue);
        System.out.println("Ключ BB существует: " + map.containsKey("BB"));
        System.out.println("\nИтоговое количество элементов: " + map.size());
        System.out.println("Коллекция пуста: " + map.isEmpty());
        System.out.println("Итоговая ёмкость: " + map.capacity());
    }
}
