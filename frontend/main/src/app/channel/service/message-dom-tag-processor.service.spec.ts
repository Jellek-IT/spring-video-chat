import { TestBed } from '@angular/core/testing';

import {
  MessageDomTagProcessorService,
  TagMessageNode,
  TextMessageNode,
} from './message-dom-tag-processor.service';
import { DOCUMENT } from '@angular/common';
import { NodeTagType } from '../enum/internal/node-tag-type.enum';

describe('MessageDomTagProcessorService', () => {
  let service: MessageDomTagProcessorService;
  let doc: Document;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessageDomTagProcessorService);
    doc = TestBed.inject(DOCUMENT);
  });

  it('should convert html to tags', () => {
    const convertedElement = doc.createElement('div');

    convertedElement.innerHTML = '<b>text</b>';
    expect(service.toTags(convertedElement)).toEqual('[b]text[/b]');

    convertedElement.innerHTML = '<i>text</i>';
    expect(service.toTags(convertedElement)).toEqual('[i]text[/i]');

    convertedElement.innerHTML = '<u>text</u>';
    expect(service.toTags(convertedElement)).toEqual('[u]text[/u]');

    convertedElement.innerHTML = '<div>text</div>';
    expect(service.toTags(convertedElement)).toEqual('[l]text[/l]');

    convertedElement.innerHTML = 'text<br>text<br>text';
    expect(service.toTags(convertedElement)).toEqual('text[nl/]text[nl/]text');

    convertedElement.innerHTML = '<span>[]</span>';
    expect(service.toTags(convertedElement)).toEqual('[l][sb/][sbe/][/l]');

    //nested structure
    convertedElement.innerHTML =
      '<b>[]<i>text<u>text</u><u>text</u></i></b><span>text</span>';
    expect(service.toTags(convertedElement)).toEqual(
      '[b][sb/][sbe/][i]text[u]text[/u][u]text[/u][/i][/b][l]text[/l]'
    );

    //clear empty tags
    convertedElement.innerHTML = '<i> <b></b><u>text<b></b></u></i>';
    expect(service.toTags(convertedElement)).toEqual('[i][u]text[/u][/i]');
  });

  it('should convert tags to text nodes', () => {
    let input = '[b]text[/b]';
    expect(service.toTextNodes(input)).toEqual([
      new TagMessageNode(NodeTagType.BOLD, {}, [new TextMessageNode('text')]),
    ]);

    input = '[u]text[/u]';
    expect(service.toTextNodes(input)).toEqual([
      new TagMessageNode(NodeTagType.UNDERLINE, {}, [
        new TextMessageNode('text'),
      ]),
    ]);

    input = '[i]text[/i]';
    expect(service.toTextNodes(input)).toEqual([
      new TagMessageNode(NodeTagType.ITALIC, {}, [new TextMessageNode('text')]),
    ]);

    input = '[l]text[/l]';
    expect(service.toTextNodes(input)).toEqual([
      new TagMessageNode(NodeTagType.LINE, {}, [new TextMessageNode('text')]),
    ]);

    input = '[nl/][sb/][sbe/]';
    expect(service.toTextNodes(input)).toEqual([
      new TagMessageNode(NodeTagType.NEW_LINE, {}, []),
      new TagMessageNode(NodeTagType.SQUARE_BRACKET, {}, []),
      new TagMessageNode(NodeTagType.SQUARE_BRACKET_END, {}, []),
    ]);

    // nested
    input = '[b][sb/][sbe/][i]text[u]text[/u][u]text[/u][/i][/b][l]text[/l]';
    expect(service.toTextNodes(input)).toEqual([
      new TagMessageNode(NodeTagType.BOLD, {}, [
        new TagMessageNode(NodeTagType.SQUARE_BRACKET, {}, []),
        new TagMessageNode(NodeTagType.SQUARE_BRACKET_END, {}, []),
        new TagMessageNode(NodeTagType.ITALIC, {}, [
          new TextMessageNode('text'),
          new TagMessageNode(NodeTagType.UNDERLINE, {}, [
            new TextMessageNode('text'),
          ]),
          new TagMessageNode(NodeTagType.UNDERLINE, {}, [
            new TextMessageNode('text'),
          ]),
        ]),
      ]),
      new TagMessageNode(NodeTagType.LINE, {}, [new TextMessageNode('text')]),
    ]);

    //undefined tag
    input = '[z][z/]';
    expect(service.toTextNodes(input)).toEqual([]);
  });
});
