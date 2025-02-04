export enum NodeTagType {
  LINE = 'l',
  NEW_LINE = 'nl',
  SQUARE_BRACKET = 'sb',
  SQUARE_BRACKET_END = 'sbe',
  BOLD = 'b',
  ITALIC = 'i',
  UNDERLINE = 'u',
}

export const isSelfClosing = (type: NodeTagType) => {
  return [NodeTagType.NEW_LINE].includes(type);
};
export default {
  isSelfClosing,
};
