import { ErrorResponseType } from '../enum/error-response-type.enum';

export interface ExceptionResponse {
  message: string;
  date: Date;
  types: {
    type: ErrorResponseType;
    values: string[];
  }[];
}

export const getExistingTypes = (
  exceptionResponse: ExceptionResponse | null
): ErrorResponseType[] => {
  return (
    exceptionResponse?.types
      .map((type) => type.type)
      .filter((errorType) =>
        Object.keys(ErrorResponseType).includes(errorType)
      ) ?? []
  );
};

export default {
  getExistingTypes,
};
