import WidgetKit

@objc(WidgetUpdater)
class WidgetUpdater: NSObject {

  @objc(getAppWidgetIds:withResolver:withRejecter:)
  func getAppWidgetIds(widgetClasses: NSArray, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    if (widgetClasses.count == 0) {
      reject("ENOARG", "No widget classes passed to getAppWidgetIds", nil)
      return
    }

    if #available(iOS 14.0, *) {
      WidgetCenter.shared.getCurrentConfigurations { results in
        guard let widgets = try? results.get() else {
          reject("ERR", "Unable to retrieve widget information", nil)
          return
        }

        var data: [String: Array<Int>] = [:]

        for widget in widgets {
          if (widgetClasses.contains(widget.kind)) {
            if (data.keys.contains(widget.kind)) {
              data[widget.kind]?.append(widget.hashValue)
            } else {
              data[widget.kind] = [widget.hashValue]
            }
          }
        }
        print("Returning widget ids: \(data)")

        resolve(data)
      }
    } else {
      reject("ENOSUP", "Updating widgets is supported on iOS >= 14", nil)
    }
  }

  @objc(updateAppWidgets:withResolver:withRejecter:)
  func updateAppWidgets(widgetClasses: NSArray, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    if (widgetClasses.count == 0) {
      reject("ENOARG", "No widget classes passed to updateAppWidgets", nil)
      return
    }

    print("Updating app widgets")

    if #available(iOS 14.0, *) {
      for widget in widgetClasses {
        guard let widgetClassName = widget as? String else {
          reject("ENOARG", "Widget classes need to be passed as strings", nil)
          return
        }

        WidgetCenter.shared.reloadTimelines(ofKind: widgetClassName)
      }
    } else {
      reject("ENOSUP", "Updating widget is supported on iOS >= 14", nil)
      return
    }

    resolve(nil)
  }

  @objc
  static func requiresMainQueueSetup() -> Bool {
    return true
  }
}
