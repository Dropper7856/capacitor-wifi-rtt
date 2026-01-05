import { registerPlugin } from '@capacitor/core';

import type { WifiRttPlugin } from './definitions';

const WifiRtt = registerPlugin<WifiRttPlugin>('WifiRtt', {
  web: () => import('./web').then((m) => new m.WifiRttWeb()),
});

export * from './definitions';
export { WifiRtt };
