import { ExceptionResponseType } from '../enum/exception-response-type.enum';

export interface ExceptionResponse {
  message: string;
  date: Date;
  types:
    | {
        type: ExceptionResponseType;
        values: string[];
      }[]
    | null;
}

export const getExistingTypes = (
  exceptionResponse: ExceptionResponse | null
): ExceptionResponseType[] => {
  return (
    exceptionResponse?.types
      ?.map((type) => type.type)
      .filter((errorType) =>
        Object.keys(ExceptionResponseType).includes(errorType)
      ) ?? []
  );
};

export default {
  getExistingTypes,
};
