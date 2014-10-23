module Paths_Hydra (
    version,
    getBinDir, getLibDir, getDataDir, getLibexecDir,
    getDataFileName
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
catchIO = Exception.catch


version :: Version
version = Version {versionBranch = [0,8,8], versionTags = []}
bindir, libdir, datadir, libexecdir :: FilePath

bindir     = "/users/level4/1102028s/.cabal/bin"
libdir     = "/users/level4/1102028s/.cabal/lib/Hydra-0.8.8/ghc-7.6.3"
datadir    = "/users/level4/1102028s/.cabal/share/Hydra-0.8.8"
libexecdir = "/users/level4/1102028s/.cabal/libexec"

getBinDir, getLibDir, getDataDir, getLibexecDir :: IO FilePath
getBinDir = catchIO (getEnv "Hydra_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "Hydra_libdir") (\_ -> return libdir)
getDataDir = catchIO (getEnv "Hydra_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "Hydra_libexecdir") (\_ -> return libexecdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "/" ++ name)
