package org.superbiz.moviefun;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Component
public class FileStore implements BlobStore {

    Tika tika = new Tika();

    @Override
    public void put(Blob blob) throws IOException {

        File targetFile = new File("covers/"+blob.name);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(blob.inputStream, outputStream);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        File file = new File("covers/"+ name);

        if(!file.exists()){
            return Optional.empty();
        }
        return Optional.of(new Blob(name, new FileInputStream(file), tika.detect(file)));
    }

    @Override
    public void deleteAll() {

        File file = new File("covers");
    }
}
