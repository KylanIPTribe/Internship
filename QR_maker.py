import qrcode
from PIL import Image, ImageDraw, ImageFont


# __________________________________________ EDIT PATHS HERE __________________________________________
URL_OF_QRCODE = "ip-tribe.com"
URL_DEFAULT = "google.com"
PATH_TO_QRCODE = "qrcode.png"
PATH_TO_CARD = "card.png"
PATH_TO_CARD_QR = "card_qr.png"
PATH_TO_CARD_QR_TEXT = "card_qr_text.png"
# __________________________________________ EDIT QR-CODE DETAILS HERE __________________________________________
QR_SIZE = (260, 260)  # keep aspect ratio by ensuring dimension is same?
QR_MARGIN = 20
# __________________________________________ EDIT TEXT DETAILS HERE __________________________________________
RGB_WHITE = (255, 255, 255)
TEXT_FIELDS = [
    { "text": "PLACEHOLDER",
        "size": 40, "color": RGB_WHITE, "right_edge": 995, "vert_pos": 55 },
    { "text": "PlaceHolder",
        "size": 30, "color": RGB_WHITE, "right_edge": 995, "vert_pos": 110 },
    { "text": "+65 90123478",
        "size": 20, "color": RGB_WHITE, "right_edge": 925, "vert_pos": 190 },
    { "text": "placeholder@email.com",
        "size": 20, "color": RGB_WHITE, "right_edge": 925, "vert_pos": 260 }
]


# __________________________________________ HELPER FUNCTIONS __________________________________________
def generate_qrcode(raw_url=URL_DEFAULT, output_path_qrcode=PATH_TO_QRCODE):
    # clean & validate input
    clean_url = raw_url.strip()
    if not isinstance(clean_url, str):
        raise ValueError("Input must be a string (URL).")
    if not clean_url.startswith("https://"):
        clean_url = "https://" + clean_url
    # make qr
    qr_code = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr_code.add_data(clean_url)
    qr_code.make(fit=True)
    qr_img = qr_code.make_image(fill_color="white", back_color="transparent")
    resized_qr_img = qr_img.resize(QR_SIZE, Image.Resampling.LANCZOS)
    resized_qr_img.save(output_path_qrcode)
    return output_path_qrcode
def add_qrcode_to_card(qr_filename=PATH_TO_QRCODE, card_filename=PATH_TO_CARD, output_path_card_with_qr=PATH_TO_CARD_QR):
    # get file
    try:
        card_img = Image.open(card_filename).convert("RGBA")
        qr_img = Image.open(qr_filename).convert("RGBA")
    except Exception as e:
        raise FileNotFoundError(f"Could not load one of the images: {e}")
    # overlay qr onto card
    pos_x = card_img.width - qr_img.width - QR_MARGIN
    pos_y = card_img.height - qr_img.height - QR_MARGIN
    card_img.paste(qr_img, (pos_x, pos_y), qr_img)  # Use QR as mask for transparency
    card_img.save(output_path_card_with_qr)
    return output_path_card_with_qr

def get_horiz_pos(draw, font, right_edge, text):
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]  # Right - Left
    horiz_pos = right_edge - text_width
    return horiz_pos  # right-aligned
def add_text_to_card(card_filename=PATH_TO_CARD_QR, output_path_card_with_qr_and_text=PATH_TO_CARD_QR_TEXT):
    # get file
    try:
        card_img = Image.open(card_filename).convert("RGBA")  # Ensure transparency support
    except Exception as e:
        raise FileNotFoundError(f"Could not load one of the images: {e}")
    # overlay text onto card
    draw = ImageDraw.Draw(card_img)
    for text_field in TEXT_FIELDS:
        text = text_field["text"]
        size = text_field["size"]
        color = text_field["color"]
        right_edge = text_field["right_edge"]
        vert_pos = text_field["vert_pos"]

        font = ImageFont.load_default(size=size)  # Use default font at desired size
        draw.text((get_horiz_pos(draw, font, right_edge, text), vert_pos), text, fill=color, font=font)
    card_img.save(output_path_card_with_qr_and_text)
    return output_path_card_with_qr_and_text
# __________________________________________ MAIN FUNCTION __________________________________________
def make_card_qr_text():
    qr = generate_qrcode(
        URL_OF_QRCODE
    )
    card_qr = add_qrcode_to_card(
        qr,
        "Simple Corporate Business Card.png"
    )
    card_qr_text = add_text_to_card(
        card_qr
    )
    print("Namecard file path is: " + card_qr_text)
make_card_qr_text()