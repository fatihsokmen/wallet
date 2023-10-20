# Deblock Wallet

## Tools / Env.

- Android Studio Iguana | 2023.2.1 Canary 7 (on Apple M1 Chip)
- Gradle 8.0

## Tests

- `./gradlew test`
- `./gradlew connectedAndroidTest`

## Things to improve/consider 

- Input field can be supported by `VisualTransformation`s that we can format/mask text value
- Should have enough ETH to cover fee? I skip it in the example. Ex: 10 ETH in wallet balance, but fee is 0.02 ETH. So wallet should have at least 10.02 ETH to cover max ETH transfer
- When `Max X ETH` text should be clickable as other defi wallets.
- Gas fee should be updated after a time span. (ex: 5 sec), however in the example, app fetches most recent fee data on each input.
- Swicthing input mode: ETH input should start from exchanged amount. In the example coide, app keep existing input value.
