import { Injectable } from '@angular/core';
import Janus, { JanusJS } from '../../../packages/janus';
import adapter from 'webrtc-adapter';

@Injectable({
  providedIn: 'root',
})
export class JanusClientFactory {
  public async create(
    options: JanusJS.ConstructorOptions,
    { debug }: { debug: boolean }
  ): Promise<Janus> {
    return new Promise((resolve) => {
      // initialized library will call callback immediately
      Janus.init({
        debug: debug ? 'all' : false,
        dependencies: Janus.useDefaultDependencies({ adapter: adapter }),
        callback: () => {
          resolve(new Janus(options));
        },
      });
    });
  }
}
