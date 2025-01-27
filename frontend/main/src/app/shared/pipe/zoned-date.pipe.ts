import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { ProjectConstants } from '../constants/project-constants';

@Pipe({
  name: 'zonedDate',
  standalone: true,
})
export class ZonedDatePipe extends DatePipe implements PipeTransform {
  public override transform(
    value: null | undefined,
    format?: string,
    locale?: string
  ): null;
  public override transform(
    value: string | number | Date,
    format?: string,
    locale?: string
  ): string | null;
  public override transform(
    value: Date | string | number | null | undefined,
    format?: string,
    locale?: string
  ): string | null {
    if (!value) {
      return null;
    }
    const offset = new Intl.DateTimeFormat('en', {
      timeZone: ProjectConstants.TIME_ZONE,
      timeZoneName: 'shortOffset',
    })
      .formatToParts(new Date(value))
      .find((part) => part.type === 'timeZoneName')!.value;

    return super.transform(value, format, offset, locale);
  }
}
