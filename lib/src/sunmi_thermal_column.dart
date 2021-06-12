import 'package:sunmi_thermal_printer/src/sunmi_thermal_align.dart';

class SunmiThermalColumn {
  SunmiThermalColumn({
    this.text = '',
    this.width = 2,
    this.align = SunmiThermalAlign.left,
  }) {
    if (width < 1 || width > 12) {
      throw Exception('Column width must be between 1 to 12');
    }
  }

  String text;
  int width;
  SunmiThermalAlign align;

  Map<String, String> toJson() {
    return {
      "text": text,
      "width": width.toString(),
      "align": align.value.toString(),
    };
  }
}
