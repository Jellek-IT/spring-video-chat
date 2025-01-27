import { Component, Input } from '@angular/core';
import {
  MessageDomTagProcessorService,
  MessageNode,
  TagMessageNode,
  TextMessageNode,
} from '../../../service/message-dom-tag-processor.service';
import { CommonModule } from '@angular/common';
import { NodeTagType } from '../../../enum/internal/node-tag-type.enum';

@Component({
  selector: 'app-message-text-node',
  imports: [CommonModule],
  templateUrl: './message-text-node.component.html',
  styleUrl: './message-text-node.component.scss',
})
export class MessageTextNodeComponent {
  @Input({ required: true })
  public node!: MessageNode;
  protected readonly nodeTagTypes = NodeTagType;

  protected isTextNode() {
    return this.node instanceof TextMessageNode;
  }

  protected isTagNode(nodeTagType: NodeTagType) {
    return this.node instanceof TagMessageNode && this.node.tag === nodeTagType;
  }

  protected nodeAsTagNode(): TagMessageNode {
    console.assert(this.node instanceof TagMessageNode);
    return this.node as TagMessageNode;
  }

  protected nodeAsTextNode(): TextMessageNode {
    console.assert(this.node instanceof TextMessageNode);
    return this.node as TextMessageNode;
  }
}
