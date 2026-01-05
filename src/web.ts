import { WebPlugin } from '@capacitor/core';

import type { WifiRttPlugin } from './definitions';

export class WifiRttWeb extends WebPlugin implements WifiRttPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
