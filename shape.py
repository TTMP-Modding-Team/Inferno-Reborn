import sys
import argparse
import json
import pyclip


parser = argparse.ArgumentParser(description='what the hell is this?')
parser.add_argument('filename')
parser.add_argument('-f', '--facing', action='store_true')

args = parser.parse_args()

filename = args.filename
facing = args.facing

def translate(box, rot):
    if rot == 0:
        return box
    elif rot == 1:
        return [[16-box[0][0], box[0][1], 16-box[0][2]], [16-box[1][0], box[1][1], 16-box[1][2]]]
    elif rot == 2:
        return [[box[0][2], box[0][1], 16-box[0][0]], [box[1][2], box[1][1], 16-box[1][0]]]
    elif rot == 3:
        return [[16-box[0][2], box[0][1], box[0][0]], [16-box[1][2], box[1][1], box[1][0]]]
    else:
        raise Exception('Invalid rotation: '+str(rot))

def box_to_model(box, rot):
    box = translate(box, rot)
    return 'box('+str(box[0][0])+', '+str(box[0][1])+', '+str(box[0][2])+', '+str(box[1][0])+', '+str(box[1][1])+', '+str(box[1][2])+')'

def to_model(boxes, rot):
    length = len(boxes)
    if length == 0:
        return 'VoxelShapes.empty()'
    elif length == 1:
        return box_to_model(boxes[0], rot)
    else:
        s = 'VoxelShapes.or('
        for i, box in enumerate(boxes):
            if i > 0:
                s += ', '
            s += box_to_model(box, rot)
        return s+').optimize()'

def make_and_copy_shape(json):
    boxes = []
    for i, e in enumerate(json['elements']):
        if 'rotation' in e:
            rotation = e['rotation']
            if rotation['angle'] != 0:
                print('Skipping element ['+str(i)+'] because of rotation')
                continue
        boxes.append([e['from'], e['to']])
    s = None
    if facing:
        s = ''
        for i, name in enumerate(['NORTH', 'SOUTH', 'WEST', 'EAST']):
            if i > 0:
                s += '\n'
            s += 'private static final VoxelShape SHAPE_'+name+' = '+to_model(boxes, i)+';'
    else:
        s = 'private static final VoxelShape SHAPE = '+to_model(boxes, 0)+';'

    pyclip.copy(s)
    print('Copied shape to clipboard')


if filename is None:
    # prompt mode
    make_and_copy_shape(json.loads(input("Paste model JSON: ")))
else:
    f = None
    path = None
    try:
        path = 'src/main/resources/assets/infernoreborn/models/block/'+filename+'.json'
        f = open(path)
    except OSError as e:
        try:
            path = 'src/generated/resources/assets/infernoreborn/models/block/'+filename+'.json'
            f = open(path)
        except:
            print('Cannot find model file named "'+filename+'"')
            sys.exit(1)
    print('Reading model file from "'+path+'"')
    make_and_copy_shape(json.load(f))

sys.exit(0)