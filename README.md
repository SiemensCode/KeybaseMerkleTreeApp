# KeybaseMerkleTreeApp
Prompts the user for a Keybase username then computes the user ID (see [user ID v2 here](https://keybase.io/docs/api/1.0/ids)). 
Looks up a Merkle root with a given hash(currently hardcoded, obtained from a [merkle/root](https://keybase.io/docs/api/1.0/call/merkle/root) call), then walks the Merkle tree down to that user's leaf, verifying each node's hash against that node's children along the way.

Walks the tree using [merkle/block API](https://keybase.io/docs/api/1.0/call/merkle/block).

