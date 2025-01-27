import { inject, Injectable } from '@angular/core';
import { PrimeNG } from 'primeng/config';

type ColorBrightness =
  | 50
  | 100
  | 200
  | 300
  | 400
  | 500
  | 600
  | 700
  | 800
  | 900
  | 950;

@Injectable({
  providedIn: 'root',
})
export class ColorService {
  private readonly chosenColors = [
    'emerald',
    'green',
    'lime',
    'red',
    'orange',
    'amber',
    'yellow',
    'teal',
    'cyan',
    'sky',
    'blue',
    'indigo',
    'violet',
    'purple',
    'fuchsia',
    'pink',
    'rose',
  ];
  private readonly primeNG = inject(PrimeNG);

  private getModulo(uuid: string, divisor: number) {
    return uuid
      .replace(/-/g, '')
      .split('')
      .reduce((remainder, char) => {
        return (remainder * 16 + parseInt(char, 16)) % divisor;
      }, 0);
  }

  private getColorVarPaletFromUUID = (
    uuid: string
  ): Record<ColorBrightness, string> => {
    const colorName =
      this.chosenColors[this.getModulo(uuid, this.chosenColors.length)];
    return this.primeNG.theme().preset.primitive[colorName];
  };

  public getColorVarFromUUID = (uuid: string) => {
    return this.getColorVarPaletFromUUID(uuid)[800];
  };

  public getLightColorVarFormUUID = (uuid: string) => {
    return this.getColorVarPaletFromUUID(uuid)[600];
  };
}
