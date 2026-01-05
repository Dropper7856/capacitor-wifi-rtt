export interface WifiRttPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
