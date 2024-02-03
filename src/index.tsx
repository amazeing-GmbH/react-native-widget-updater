import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-widget-updater' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export type AppWidgetIds = Record<string, number[]>;

export type WidgetUpdaterModuleType = {
  getAppWidgetIds: { (widgetClass: string[]): Promise<AppWidgetIds> };
  updateAppWidgets: { (widgetClass: string[]): Promise<void> };
};

const WidgetUpdater: WidgetUpdaterModuleType = NativeModules.WidgetUpdater
  ? NativeModules.WidgetUpdater
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export async function getAppWidgetIds(
  widgetClasses: string[]
): Promise<AppWidgetIds> {
  return WidgetUpdater.getAppWidgetIds(widgetClasses);
}

export async function updateAppWidgets(widgetClasses: string[]): Promise<void> {
  return WidgetUpdater.updateAppWidgets(widgetClasses);
}

export default WidgetUpdater;
