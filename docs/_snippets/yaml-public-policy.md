By default, each slide deck applies custom settings defined in a dedicated PITCHME.yaml file that is co-located in the same directory as the [PITCHME.md](conventions/pitchme-md.md) markdown for that deck.

For **slide decks within public repositories** if no dedicated PITCHME.yaml settings file is found, decks will render having automatically *inherited* any settings defined in the PITCHME.yaml file located in the root directory of that same repository.

!> This public repo fallback policy allows you to maintain a single PITCHME.yaml in the root directory and have those settings shared by multiple decks within a single branch.

