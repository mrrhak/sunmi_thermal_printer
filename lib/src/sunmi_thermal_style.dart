import 'package:sunmi_thermal_printer/src/sunmi_thermal_align.dart';
import 'package:sunmi_thermal_printer/src/sunmi_thermal_size.dart';

class SunmiThermalStyles {
  final bool bold;
  final bool underline;
  final SunmiThermalAlign align;
  final SunmiThermalSize size;

  const SunmiThermalStyles({
    this.bold = false,
    this.underline = false,
    this.align = SunmiThermalAlign.left,
    this.size = SunmiThermalSize.md,
  });
}
