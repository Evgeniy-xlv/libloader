package ru.xlv.libloader.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LibLoaderTransformer implements IClassTransformer {

    private final Logger logger = Logger.getLogger(LibLoaderTransformer.class.getSimpleName());

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        if(classNode.visibleAnnotations != null) {
            for (AnnotationNode visibleAnnotation : classNode.visibleAnnotations) {
                if (visibleAnnotation.desc.equals("Lnet/minecraftforge/fml/common/Mod;")) {
                    System.out.println(name);
                    logger.info("Found a mod depends on some libraries: " + name);
                    for (AnnotationNode visibleAnnotation1 : classNode.visibleAnnotations) {
                        if(visibleAnnotation1.desc.equals("Lru/xlv/libloader/DependsOn;")) {
                            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                            if (visibleAnnotation1.values.size() > 1 && visibleAnnotation1.values.get(1) instanceof ArrayList) {
                                List<String> urls = (ArrayList<String>) visibleAnnotation1.values.get(1);
                                for (String url : urls) {
                                    logger.info("- Downloading: " + url);
                                    File file = new File("mods", url.substring(url.lastIndexOf("/") + 1));
                                    try {
                                        BufferedInputStream bufferedInputStream = new BufferedInputStream(new URL(url).openStream());
                                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                                        byte[] dataBuffer = new byte[1024];
                                        int bytesRead;
                                        while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1) {
                                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                                        }
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                        bufferedInputStream.close();
                                        Loader.instance().getModClassLoader().addFile(file);
                                        logger.info("- Done.");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                logger.info("All libraries have been successfully downloaded and installed.");
                                classNode.accept(classWriter);
                                return classWriter.toByteArray();
                            }
                        }
                    }
                }
            }
        }
        return basicClass;
    }
}
