import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});
  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final _counter = ValueNotifier<int>(0);
  final _methodChannel = const MethodChannel('androidAuto');
  final _eventChannel = const EventChannel('androidAutoStatus');

  void _incrementCounter() {
    _counter.value++;
    _methodChannel.invokeMethod('setCounter', {'counter': _counter.value});
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _methodChannel.setMethodCallHandler((call) async {
        debugPrint('Method: ${call.method}');
        if (call.method == 'setCounter') {
          if (call.arguments case {'counter': final counter}) {
            _counter.value = int.parse(counter.toString());
            _methodChannel.invokeMethod('setCounter', {'counter': _counter.value});
          }
        }
      });
      _eventChannel.receiveBroadcastStream().listen((event) {
        debugPrint('Event: $event');
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter Demo'),
      ),
      body: ValueListenableBuilder(
          valueListenable: _counter,
          builder: (context, counter, child) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Text(
                    'You have pushed the button this many times:',
                  ),
                  Text(
                    '$counter',
                    style: Theme.of(context).textTheme.headlineMedium,
                  ),
                ],
              ),
            );
          }),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}
