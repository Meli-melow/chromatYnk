# chromatYnk_main
 Academic software project in Java aiming to create a drawing interface

Within 3 weeks, me and 4 other students created an app able to create drawing from a set of instructions or a file.
Instructions are written in a new language.
After launching the app, you can create cursors and draw lines by executing instructions!


Set of instructions :
I) Cursors
- CURSOR id : creates a cursor identified by 'id' (an int), they are first located on the center;
- SELECT id : selects cursor identified by 'id', required to start drawing;
- REMOVE id : erases cursor identified by 'id'.


II) Variables
- Names start only with a letter;
- You can initialize them, no reassignement (we didn't have the time, so they're constants technically '^^);
- NUM name : creates a float variable 'name' (use positive values only);
- STR name : creates a string variable 'name';
- BOOL name : creates a boolean variable 'name'.


III) Use of cursors
- Please note that only non-empty drawings can be saved;
- (0,0) is the top-left corner so use positive values only;
- these instructions are applied to the lattest selected cursor;
- FWD value[/%] : (forward) moves forward (to the right) by 'value' pixels / percentage of the biggest dimension
of the drawing area (either the width or the height), '/' means only one method can be used;
- BWD value[/%] : FWD backwards;
- TURN value : changes orientation clockwise;
- POS x[/%] y[/%] : relocates to (x,y) / (x0,y0) being percentages of the width and the height respectively, use is
similar to FWD (more info about the use of variables in VII. and VIII. sections);
- MOV x[/%] y[/%] : moves the selected cursor relatively to (x,y) / (x0,y0), use like POS;
- LOOKAT id : changes orientation so it points at the cursor identified by 'id';
- LOOKAT x[/%] y[/%] : changes orientation so it points at the cursor located at (x,y) / (x0,y0) being percentages 
of the width and the height respectively, use is similar to POS (more info about the use of variables in VI. and VII.
sections);
- HIDE : hides the selected cursor.


IV) Outline
- these instructions are applied to the lattest selected cursor;
- COLOR R G B[/#RRGGBB] : changes the color using rgb int values (from 0 to 255) / web format, incompatible with
variables;
- THICK value : modifies outline's thickness;
- PRESS value[/%] : modifies opacity, use similar to FWD (use of variables is uncertain).


V) Instructions blocks
- are placed before a set of instructions;
- can be used in a .txt file;
- FOR var : iterates a set of instructions 'var' times, 'var' CAN'T be a variable;
- WHILE var : while loop that will iterate at most 10000 times (can be changed in interpreter.java or interface.java);
- MIRROR x1 y1 : will reproduce instructions of each selected in the block with axial symmetry, will start with the
defined cursor at (x1,y1).
- N.B : MIRROR and FOR loops CANNOT be interlocked.


VI) Use, recommended + required :
- SceneBuilder for visualization + download the App.fxml file + all the .java files (deployment is uncertain);
- IntelliJ Idea or Eclipse as IDE;
- the window is not responsive but is scrollable (I am open to any framework suggestion well-documented and updated,
ScenBuilder was a VERY HARD experience...);
- final version is in 'rendu-ProjetGroupe28-main' folder but 'main-PresqueComplet' v2 and v3 are exploitable.


VII) What can be done :
- executing instructions with 'RUN' button;
- change the background color with the button above 'RUN';
- deleting the drawing area with the 'DELETE' button (WARNING : it erases ALL cursors);
- choose execution speed with the cursor bar (min 2 ms, max 1 sec);
- choose to stop the execution as soon as an error was detected (tick 'ignore erreors');
- saving a non-empty drawing with the 'File' button above the drawing area (cursors will be hidden automatically ;);
- track your actions with the app history;
- use float variables with one-parameter instruction (see III. to  V. sections);
- variables declarations and assignations;


VIII) What can be improved :
- addind x/y-axis + a grid;
- cursors' shape, notably to recognize their current angle;
- restricting cursors' ids to positive ints;
- reassigning variables, notably to improving the use of WHILE loops;
- implement a purpose for STR variables (they could be instructions ?);
- improving the parsing of instructions involving at least 2 variables (see the interpreter.java file);
- routing, the final version has only one view being App.fxml;
- interlocking MIRROR and FOR loops;
- and more!