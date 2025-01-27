import { ZonedDatePipe } from './zoned-date.pipe';

describe('ZonedDatePipe', () => {
  it('create an instance', () => {
    const pipe = new ZonedDatePipe();
    expect(pipe).toBeTruthy();
  });
});
