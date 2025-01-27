export const clamp = (number: number, min: number, max: number) => {
  return Math.max(Math.min(number, max), min);
};

export default {
  clamp,
};
