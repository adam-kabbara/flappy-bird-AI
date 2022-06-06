# Flappy Bird AI

This is an attempt at creating an AI to play flappy bird using ```java``` The AI learns using a simple genetic algorithm that implements concepts like crossing over and mutations to several neural networks
<img align="right" src="https://user-images.githubusercontent.com/62020687/172265866-3c93ac92-d26a-4f49-bd1e-dfc6a63d590b.gif">

This project was inspired by [Code Bullet](https://www.youtube.com/watch?v=WSW-5m8lRMs) and [The Coding Train](https://www.youtube.com/watch?v=c6y21FkaUqw)

## Imports

This project was done mostly from scratch without the use of any external libraries like ```TensorFlow```

```java
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
```

## Structure
The lowest level class is the ```Matrix.java``` class which, as the name suggests, is a class that mimics the functionality of matrices

Then there's the ```NeuralNetwork.java``` class which implements the neural networks and contains methods like ```predict``` that make the bird decide to jump or not

After that, there are the higher-level classes that represent the objects seen on the screen. These classes are ```Pipe.java``` ```Base.java``` and ```Bird.java```

Finally, there is the highest level class ```mainWindow.java``` which contains the game loop and the logic for the genetic algorithm

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
