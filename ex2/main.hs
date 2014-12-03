import System.Directory
import Graphics.HsExif
import Data.List
import Data.Maybe
import Data.Ord
import Data.Time.LocalTime

getFilesInDirectory :: IO [FilePath]
getFilesInDirectory = fmap (filter isPic) (getDirectoryContents ".")
    
isPic :: FilePath -> Bool
isPic f = (take 3 f) == "pic"

getMeta :: FilePath -> IO (Maybe LocalTime)
getMeta fp = do
    meta <- parseFileExif fp 
    return $
        case meta of
            Right m -> getDateTimeOriginal m
            _ -> Nothing

sortFiles :: (FilePath, Maybe LocalTime) -> (FilePath, Maybe LocalTime)
sortFiles fm  = sortBy (comparing snd) hasTimes ++ hasNoTimes
    where
        hasTimes = filter (isJust . snd) fm
        hasNoTimes = filter (isNothing . snd) fm

main :: IO ()
main = do
    files <- getFilesInDirectory
    metadata <- mapM getMeta files
    fileMeta <- sortFiles $ zip files metadata
    putStrLn (show metadata)
