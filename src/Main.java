import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new TreeMap<>();
    public static boolean flag = true;

    public static void main(String[] args) {
        int THREADS = 1000;
        Runnable countThread = () -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    Map.Entry<Integer, Integer> maxEntry = sizeToFreq.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .orElse(null);
                    System.out.println("Map sizeToFreq обновлена, текущий максимум: " + Objects.requireNonNull(maxEntry).getKey());
                }
            }
        };
        Thread thread1 = new Thread(countThread);
        thread1.start();
        for (int i = 0; i < THREADS; i++) {
            Runnable logic = () -> {
                String string = generateRoute("RLRFR", 100);
                int res = (int) string.chars()
                        .filter(c -> c == 'R')
                        .count();
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(res)) {
                        int count = sizeToFreq.get(res);
                        sizeToFreq.put(res, ++count);
                    } else sizeToFreq.put(res, 1);
                    sizeToFreq.notify();
                }
            };
            Thread thread = new Thread(logic);
            thread.start();
            if (i == THREADS - 1) {
                thread1.interrupt();
            }
        }

        sizeToFreq.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .forEach(Main::print);
    }

    private static void print(Map.Entry<Integer, Integer> integerIntegerEntry) {
        if (flag) {
            System.out.println("Самое частое количество повторений " + integerIntegerEntry.getKey() + " (встретилось " + integerIntegerEntry.getValue() + " раз)");
            flag = false;
            System.out.println("Другие размеры:");
        } else {
            System.out.println("- " + integerIntegerEntry.getKey() + " (" + integerIntegerEntry.getValue() + " раз)");

        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
