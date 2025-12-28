Integration notes
-----------------

This workspace contains a C++ poker engine under `cpp/` and a Java TUI under `java/`.

Build C++ engine:

```bash
cd cpp
make
```

This produces `cpp/bin/poker_engine`.

Run the Java application (from project root):

```bash
javac -d out $(find java/src -name "*.java")
java -cp out com.poker.PokerApplication
```

The Java app will try to start the C++ engine automatically if it is available at `./cpp/bin/poker_engine`.
You can override the path with environment variable `POKER_ENGINE_PATH`.

If the C++ engine is not available the Java application will fallback to its internal evaluator.

