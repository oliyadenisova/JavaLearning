
package ru.oliya.learning.userservice;

import ru.oliya.learning.userservice.dao.UserDao;
import ru.oliya.learning.userservice.entity.User;
import ru.oliya.learning.userservice.service.UserService;
import ru.oliya.learning.userservice.util.HibernateUtil;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            UserDao userDao = new UserDao();
            UserService userService = new UserService(userDao);
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Выберите действие: ");
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        createUser(scanner, userService);
                        break;
                    case "2":
                        findUserById(scanner, userService);
                        break;
                    case "3":
                        showAllUsers(userService);
                        break;
                    case "4":
                        updateUser(scanner, userService);
                        break;
                    case "5":
                        deleteUser(scanner, userService);
                        break;
                    case "0":
                        running = false;
                        System.out.println("Приложение завершено.");
                        break;
                    default:
                        System.out.println("Некорректный выбор. Попробуйте ещё раз.");
                }
                System.out.println();
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }

    private static void createUser(Scanner scanner, UserService userService) {
        System.out.println("\n--- Создание пользователя ---");
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        String email;
        while (true) {
            System.out.print("Введите email: ");
            email = scanner.nextLine().trim();
            try {
                if (email.isBlank()) {
                    throw new IllegalArgumentException(
                            "Email не может быть пустым"
                    );
                }
                if (!email.matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                )) {
                    throw new IllegalArgumentException(
                            "Некорректный формат email"
                    );
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Попробуйте ещё раз.");
            }
        }
        Integer age = null;
        boolean validAge = false;
        do {
            System.out.print("Введите возраст: ");
            String ageInput = scanner.nextLine();
            try {
                age = Integer.parseInt(ageInput);
                if (age < 0) {
                    System.out.println(
                            "Ошибка: возраст не может быть отрицательным. Попробуйте ещё раз."
                    );
                } else {
                    validAge = true;
                }
            } catch (NumberFormatException e) {
                System.out.println(
                        "Ошибка: введите возраст целым числом."
                );
            }
        } while (!validAge);
        try {
            User user = new User(name, email, age);
            userService.save(user);
            System.out.println("\nПользователь успешно создан!");
            System.out.println("ID: " + user.getId());
            System.out.println(user);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println(
                    "Не удалось сохранить пользователя: " + e.getMessage()
            );
        }
    }

    private static void findUserById(Scanner scanner, UserService userService) {
        System.out.println("\n--- Поиск пользователя ---");
        while (true) {
            System.out.print("Введите ID пользователя (0 — назад в меню): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("Возврат в меню.");
                return;
            }
            try {
                Long id = Long.parseLong(input);
                User user = userService.findById(id);
                if (user == null) {
                    System.out.println("Пользователь с ID " + id + " не найден.");
                    continue;
                }
                System.out.println("Пользователь найден:");
                System.out.println(user);
                return;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть целым числом.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Ошибка при поиске: " + e.getMessage());
                return;
            }
            System.out.println("Попробуйте ещё раз.\n");
        }
    }

    private static void showAllUsers(UserService userService) {
        System.out.println("\n--- Список пользователей ---");
        try {
            List<User> users = userService.findAll();
            if (users.isEmpty()) {
                System.out.println("В базе данных пока нет пользователей.");
                return;
            }
            System.out.println("Всего пользователей: " + users.size());
            for (User user : users) {
                System.out.println(user);
            }
        } catch (RuntimeException e) {
            System.out.println(
                    "Не удалось получить список пользователей: " + e.getMessage()
            );
        }
    }

    private static void updateUser(Scanner scanner, UserService userService) {
        System.out.println("\n--- Обновление пользователя ---");
        User user = null;

        // Ищем пользователя по ID
        while (user == null) {
            System.out.print("Введите ID пользователя (0 — назад в меню): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("Возврат в меню.");
                return;
            }
            try {
                Long id = Long.parseLong(input);
                user = userService.findById(id);
                if (user == null) {
                    System.out.println("Пользователь не найден. Попробуйте ещё раз.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть целым числом.\n");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + "\n");
            } catch (RuntimeException e) {
                System.out.println("Ошибка при поиске: " + e.getMessage());
                return;
            }
        }
        System.out.println("\nТекущие данные:");
        System.out.println(user);

        // Обновляем имя
        System.out.print("Новое имя (Enter — оставить прежнее): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            user.setName(name);
        }

        // Обновляем email
        while (true) {
            System.out.print("Новый email (Enter — оставить прежний): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                break;
            }
            if (!email.matches(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            )) {
                System.out.println("Ошибка: некорректный формат email.");
                continue;
            }
            user.setEmail(email);
            break;
        }

        // Обновляем возраст
        while (true) {
            System.out.print("Новый возраст (Enter — оставить прежний): ");
            String ageInput = scanner.nextLine().trim();
            if (ageInput.isEmpty()) {
                break;
            }
            try {
                int age = Integer.parseInt(ageInput);
                if (age < 0) {
                    System.out.println("Возраст не может быть отрицательным.");
                    continue;
                }
                user.setAge(age);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите возраст целым числом.");
            }
        }

        // Сохраняем изменения
        try {
            userService.update(user);
            System.out.println("\nПользователь успешно обновлён!");
            System.out.println(user);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Не удалось обновить пользователя: " + e.getMessage());
        }
    }

    private static void deleteUser(Scanner scanner, UserService userService) {
        System.out.println("\n--- Удаление пользователя ---");
        while (true) {
            System.out.print("Введите ID пользователя (0 — назад в меню): ");
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("Возврат в меню.");
                return;
            }
            try {
                Long id = Long.parseLong(input);
                User user = userService.findById(id);
                if (user == null) {
                    System.out.println("Пользователь не найден. Попробуйте ещё раз.\n");
                    continue;
                }
                System.out.println("\nПользователь для удаления:");
                System.out.println(user);
                System.out.print("Вы действительно хотите удалить пользователя? (да/нет): ");
                String confirmation = scanner.nextLine().trim();
                if (!confirmation.equalsIgnoreCase("да")) {
                    System.out.println("Удаление отменено.");
                    return;
                }
                userService.deleteById(id);
                System.out.println("Пользователь успешно удалён.");
                return;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть целым числом.\n");

            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + "\n");

            } catch (RuntimeException e) {
                System.out.println("Не удалось удалить пользователя: " + e.getMessage());
                return;
            }
        }
    }

    private static void printMenu() {
        System.out.println("================================");
        System.out.println("          USER SERVICE");
        System.out.println("================================");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.println("================================");
    }
}