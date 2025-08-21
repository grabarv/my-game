This is a manual that explains .gameobj extension.

Lines that start with # mean comments.

Lines that start with M or m indicate some meta information like object name, object size, etc.
The meta information in written in that way:
m <type> <value>
For example:
m name house1
Below are presented all types of meta information and their expected value:
    name -> string with object name (same as file name without extension)
    width -> int number with width of object in game blocks. Required field.
    height -> int number with height of object in game blocks. Required field.
    clearArea -> true or false. If value is set to true then all blocks that are in area of object but not mentioned in file would be set to null. 
        If value is set to false those blocks would be left. Default value is false.
    startX -> int number, that represents first index of blocks array for top-left corner of game object.
    startY -> int number, that represents second index of blocks array for top-left corner of game object.

    
    