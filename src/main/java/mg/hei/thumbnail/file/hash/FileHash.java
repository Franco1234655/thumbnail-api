package mg.hei.thumbnail.file.hash;

import mg.hei.thumbnail.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
