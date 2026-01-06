import { WebPlugin } from '@capacitor/core';

import type {
  WifiRttPlugin,
  IsSupportedResult,
  IsWifiEnabledResult,
  StartScanResult,
  GetScanResultsResponse,
  StartRangingOptions,
} from './definitions';

export class WifiRttWeb extends WebPlugin implements WifiRttPlugin {
  async isSupported(): Promise<IsSupportedResult> {
    return {
      supported: false,
      reason: 'Wi-Fi RTT is not supported on web platform',
    };
  }

  async isWifiEnabled(): Promise<IsWifiEnabledResult> {
    throw this.unavailable('Wi-Fi RTT is not available on web platform');
  }

  async startScan(): Promise<StartScanResult> {
    throw this.unavailable('Wi-Fi RTT is not available on web platform');
  }

  async getScanResults(): Promise<GetScanResultsResponse> {
    throw this.unavailable('Wi-Fi RTT is not available on web platform');
  }

  async startRanging(_options: StartRangingOptions): Promise<void> {
    throw this.unavailable('Wi-Fi RTT is not available on web platform');
  }

  async stopRanging(): Promise<void> {
    throw this.unavailable('Wi-Fi RTT is not available on web platform');
  }
}


