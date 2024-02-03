import * as React from 'react';

import { StyleSheet, View, Text, Button, Platform } from 'react-native';
import {
  type AppWidgetIds,
  getAppWidgetIds,
  updateAppWidgets,
} from 'react-native-widget-updater';

export default function App() {
  const [result, setResult] = React.useState<AppWidgetIds>({});

  React.useEffect(() => {
    const widgetClasses: string[] = Platform.select({
      android: ['.ExampleAppWidget'],
      default: ['WidgetUpdaterExampleWidget'],
    });
    getAppWidgetIds(widgetClasses).then((widgetIds: AppWidgetIds) =>
      setResult(widgetIds)
    );
  }, []);

  const renderResult = (): React.ReactNode => {
    const results: string[] = [];

    if (Object.keys(result).length === 0) {
      results.push(
        'Please add an example widget to the home screen and re-open the app.'
      );
    } else {
      Object.keys(result).forEach((r) => {
        results.push(`${r} => ${result[r]}`);
      });
    }

    return results.join('\n');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Result: {renderResult()}</Text>
      <Button
        title="Update widgets"
        disabled={Object.keys(result).length === 0}
        onPress={() => {
          if (Object.keys(result).length !== 0) {
            updateAppWidgets(Object.keys(result));
          }
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 32,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  text: {
    paddingBottom: 20,
  },
});
