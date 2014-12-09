import System.Directory
import Graphics.HsExif
import Data.ByteString as B
import Data.List as L
import System.IO as I
import Data.Maybe
import Data.Ord
import Data.Time.LocalTime

{-
Keir Alexander Smith 
1102028s

Functional Programming 4

Assessed Exercise 2 (Optional): Ordering images by their date taken

02/12/14
-}

{-
Status report:

The solution will look at the metadata of pictures starting with pic in the current directory then print out a list, in order, of their date taken and name.
It should also get the checksum, but it runs out of stackspace because of the way I went about the solution. I haven't tested if it works or not.
Since it's so simple there isn't much to go wrong, the only issue may crop up if the metadata is missing a field or has some oddity I haven't handled correctly. Hopefully I have handled empty date taken fields correctly, but it's untested. It should be noted that the input files are only checked to start with the word pic, it doesn't check file extension. Meaning it will break if there is something starting with "pic" in the current directory
There are no extensions, although a comamndline filepath for the directory with the pictures would have been a simple, yet useful addition.
        
-}

getFilesInDirectory :: IO [FilePath]
getFilesInDirectory = fmap (L.filter isPic) (getDirectoryContents ".")
    
isPic :: FilePath -> Bool
isPic f = (L.take 3 f) == "pic"

getMeta :: FilePath -> IO (Maybe LocalTime)
getMeta fp = do
    meta <- parseFileExif fp 
    return $
        case meta of
            Right m -> getDateTimeOriginal m
            _ -> Nothing

sortFiles :: [(FilePath, Maybe LocalTime)] -> IO [(FilePath, Maybe LocalTime)]
sortFiles fm  = return $ sortBy (comparing snd) hasTimes ++ hasNoTimes
    where
        hasTimes = L.filter (isJust . snd) fm
        hasNoTimes = L.filter (isNothing . snd) fm

main :: IO ()
main = do
    files <- getFilesInDirectory
    metadata <- mapM getMeta files
    fileMeta <- sortFiles $ L.zip files metadata
    I.putStrLn (show fileMeta)

    listofjpegs <- mapM B.readFile files
    let jpegbytes = L.map B.unpack listofjpegs
    let jpegchecksum = L.map sum jpegbytes

    I.putStrLn (show jpegchecksum)
